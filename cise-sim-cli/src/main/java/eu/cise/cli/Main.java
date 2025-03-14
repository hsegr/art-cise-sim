/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Joint Research Centre (JRC) All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.cise.cli;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.beust.jcommander.JCommander;
import eu.cise.servicemodel.v1.message.Acknowledgement;
import eu.cise.sim.engine.SendParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MINUTES;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main implements Runnable {

    private static Args args;
    Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] argv) {
        args = new Args();
        Main main = new Main();
        var cmd = JCommander.newBuilder()
            .addObject(args)
            .build();
        cmd.parse(argv);

        if (args.help) {
            cmd.usage();
            System.exit(0);
        }

        main.run();
    }

    @Override
    public void run() {
        System.setProperty("conf.dir", args.config);

        var appContext = new CliAppContext();

        var useCaseSendMessage = new UseCaseSendMessage(
            appContext.makeSimEngine(),
            appContext.makeMessagePersistence(),
            appContext.makeMessageLoader(),
            appContext.makeMessageProcessor());

        var useCaseReceiveMessage = new UseCaseReceiveMessage(
            appContext.makeSimEngine(), appContext.makeMessagePersistence()
        );


        if (args.listen) {
            port(args.port);
            post(args.contextPath, (request, response) -> {
                var xmlMapper = appContext.getXmlMapper();

                Acknowledgement ack = useCaseReceiveMessage.receive(xmlMapper.fromXML(request.body()));

                response.status(201);
                response.type("application/xml");

                return xmlMapper.toXML(ack);
            });
        } else {
            if (args.syncN > 0) {

                sendMultiSync(args.syncN, useCaseSendMessage, args.filename, args.requiresAck, args.correlationId);

            } else if (args.asyncN > 0) {

                sendMultiAsync(args.asyncN, useCaseSendMessage, args.filename, args.requiresAck, args.correlationId);

            } else {
                logger.info("Sending message {} ", args.filename);
                sendMessage(useCaseSendMessage, args.filename, args.requiresAck, args.messageId, args.correlationId);
            }
        }

    }

    private void sendMultiSync(int n, UseCaseSendMessage useCaseSendMessage, String filename, boolean requiresAck, String correlationId) {

        if (StringUtils.isEmpty(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        for (int i = 0; i < n; ++i) {
            String messageId = UUID.randomUUID().toString();
            sendMessage(useCaseSendMessage, filename, requiresAck, messageId, correlationId);
        }
    }

    private void sendMultiAsync(int n, UseCaseSendMessage useCaseSendMessage, String filename, boolean requiresAck, String correlationId) {

        ExecutorService executor = Executors.newCachedThreadPool();

        final String correlationIdNew = StringUtils.isEmpty(correlationId) ? UUID.randomUUID().toString() : correlationId;

        for (int i = 0; i < n; ++i) {
            String messageId = UUID.randomUUID().toString();
            executor.execute(() -> sendMessage(useCaseSendMessage, filename, requiresAck, messageId, correlationIdNew));
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    private void sendMessage(UseCaseSendMessage useCaseSendMessage, String filename, boolean requiresAck, String messageId, String correlationId) {
        var sendParam = new SendParam(requiresAck, messageId, correlationId);
        useCaseSendMessage.send(filename, sendParam);
    }
}
