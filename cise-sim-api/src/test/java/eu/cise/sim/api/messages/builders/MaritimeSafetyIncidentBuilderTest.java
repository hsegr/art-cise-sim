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

package eu.cise.sim.api.messages.builders;

import eu.cise.datamodel.v1.entity.anomaly.Anomaly;
import eu.cise.datamodel.v1.entity.document.VesselDocument;
import eu.cise.datamodel.v1.entity.event.Event;
import eu.cise.datamodel.v1.entity.incident.Incident;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.servicemodel.v1.message.CoreEntityPayload;
import eu.cise.servicemodel.v1.message.Message;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import eu.cise.signature.SignatureService;
import eu.cise.sim.api.messages.builders.incident.MaritimeSafetyIncidentBuilder;
import eu.cise.sim.api.messages.dto.incident.IncidentInfoDto;
import eu.cise.sim.api.messages.dto.incident.IncidentRequestDto;
import eu.cise.sim.api.messages.dto.incident.VesselInfoDto;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static eu.cise.signature.SignatureServiceBuilder.newSignatureService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MaritimeSafetyIncidentBuilderTest {

    XmlMapper xmlMapper;

    @Before
    public void before(){
        xmlMapper = new DefaultXmlMapper();
    }


    @Test
    public void build() {

        IncidentRequestDto msgRequest = new IncidentRequestDto();

        IncidentInfoDto incidentInfoDto = new IncidentInfoDto();
        incidentInfoDto.setIncidentType("maritime");
        incidentInfoDto.setSubType("ElectricalGeneratingSystemFailure");
        incidentInfoDto.setLatitude("37.9333");
        incidentInfoDto.setLongitude("23.5301");

        VesselInfoDto vesselInfoDto = new VesselInfoDto();
        vesselInfoDto.setImoNumber("11");
        vesselInfoDto.setMmsi("22");
        vesselInfoDto.setVesselType("GeneralCargoShip");
        vesselInfoDto.setRole("Participant");

        msgRequest.setIncident(incidentInfoDto);
        msgRequest.getVesselList().add(vesselInfoDto);

        MaritimeSafetyIncidentBuilder msgBuilder = new MaritimeSafetyIncidentBuilder();
        Incident incident = msgBuilder.build(msgRequest);

        XmlMapper prettyNotValidatingXmlMapper = new DefaultXmlMapper.PrettyNotValidating();
        String xml = prettyNotValidatingXmlMapper.toXML(incident);

        assertNotNull(xml);
    }


    @Test
    public void checkContentContainingEnumsWithTrailingSpaces() throws IOException {
        SignatureService signatureService = newSignatureService(xmlMapper)
                .withKeyStoreName("dummyKeystore.jks")
                .withKeyStorePassword("dummyPassword")
                .withPrivateKeyAlias("cisesim-nodeex.nodeex.eucise.ex")
                .withPrivateKeyPassword("dummyPassword")
                .build();


        String messageXml = readResource("messages/PushAnomalySensorTypeWithTrailingSpaces.xml");
        Message message = xmlMapper.fromXML(messageXml);
        Message signedMessage = signatureService.sign(message);
        System.out.println(xmlMapper.toXML(signedMessage));
        assertNotNull(signedMessage);
    }

    /**
     * There was an issue on the verification of pretty printed messages because the XMLSignature
     * element was unmarshalling the xml element counting also the spaces as xml nodes.
     * <p>
     * The fix has been released in the cise-model-generator-java.
     *
     * @throws IOException        when is not able to load the xml file
     * @throws URISyntaxException when the file name has issues.
     */
    @Test
    public void it_loads_an_xml_with_trailing_spaces_on_enumerations()
            throws Exception {
        String messageXML = readResource("messages/PushAnomalySensorTypeWithTrailingSpaces.xml");

        Message message = xmlMapper.fromXML(messageXML);

        System.out.println(getSensorType(message));

        Document messageDOM = xmlMapper.toDOM(message);
//        printTree(messageDOM, "");
        Message messageFromDOM = xmlMapper.fromDOM(messageDOM);

        assertEquals(SensorType.ACOUSTIC_SYSTEMS, getSensorType(messageFromDOM));

    }


    private String readResource(String resourceName) throws IOException {
        Path path = Paths.get(getResourceURI(resourceName));
        return Files.readString(path);
    }

    private URI getResourceURI(String resourceDir) {
        try {
            return this.getClass().getClassLoader().getResource(resourceDir).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private SensorType getSensorType(Message message) {
        CoreEntityPayload payload = message.getPayload();
        Anomaly anomaly = ((Anomaly)((XmlEntityPayload) payload).getAnies().get(0));
        List<Event.InvolvedObjectRel> involvedObjectRels = anomaly.getInvolvedObjectRels();
        Event.InvolvedObjectRel involvedObjectRel = involvedObjectRels.get(0);
        Objet objet = involvedObjectRel.getObject();
        Objet.LocationRel locationRel = objet.getLocationRels().get(0);
        return locationRel.getSensorType();
    }

    public static void printTree(Node doc, String indent) {
        if (doc == null) {
            System.out.println("Nothing to print!!");
            return;
        }
        try {
            System.out.println(indent + doc.getNodeName() + "  \"" + doc.getNodeValue()+"\"");
            NodeList nl = doc.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                printTree(node, indent + "  ");
            }
        } catch (Throwable e) {
            System.out.println("Cannot print!! " + e.getMessage());
        }
    }





}