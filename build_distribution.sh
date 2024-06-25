#!/bin/bash

#
# BSD 3-Clause License
#
# Copyright (c) 2021, Joint Research Centre (JRC) All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# 3. Neither the name of the copyright holder nor the names of its
#    contributors may be used to endorse or promote products derived from
#    this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
#

docker build -t ec-jrc/cise-sim:latest .

cd target
mkdir dist
cd dist

cp ../cise-sim-1.4.5-bin.tar.gz ./
cp ../../README.md ./

echo 'docker load < docker_cisesim_latest.tar.gz' > docker_install.sh
chmod +x ./docker_install.sh
# create conf/logs/msghistory directories
mkdir -p conf
# copy sample contents within conf directory
cp ../../cise-sim-dropw/src/main/resources/dummyKeystore.jks conf/
cp ../../cise-sim-dropw/src/main/resources/config.yml conf/
cp ../../cise-sim-dropw/src/main/resources/sim.properties conf/
cp -r ../../cise-sim-assembly/src/main/resources/templates templates
mkdir -p logs
mkdir -p msghistory
# copy the docker-compose script to use
cp ../../docker-compose.yml .
docker save ec-jrc/cise-sim:latest | gzip > ./docker_cisesim_latest.tar.gz

tar -cvf ../cise-sim-distribution.tar *
cd ..
rm -rf dist
cd ..
