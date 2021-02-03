# Vige, Home of Professional Open Source Copyright 2010, Vige, and
# individual contributors by the @authors tag. See the copyright.txt in the
# distribution for a full listing of individual contributors.
# Licensed under the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may obtain
# a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:13.0.1-jdk
EXPOSE 8080
RUN yum -y update && \
	yum -y install sudo && \
    echo "%wheel ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    useradd -u 1000 -G users,wheel -d /home/votinguser --shell /bin/bash -m votinguser && \
    echo "votinguser:secret" | chpasswd && \
    yum -y update && \
    yum clean all && \
    yum -y autoremove

USER votinguser

ENV TERM xterm

WORKDIR /workspace
COPY / /workspace/vota
RUN sudo chown -R votinguser:votinguser /workspace
RUN cd vota && ./gradlew build -x test
RUN rm -Rf /home/votinguser/.gradle && \
	mv /workspace/vota/build/libs/voting*.jar /workspace/vota.jar && \
	rm -Rf /workspace/vota

CMD java -jar /workspace/vota.jar --server.port=8080 --spring.profiles.active=docker && \
	tail -f /dev/null
