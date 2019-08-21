# Licensed to the Apache Software Foundation (ASF) under one or more contributor
# license agreements; and to You under the Apache License, Version 2.0.

FROM scala

ENV UID=1001 \
    NOT_ROOT_USER=owuser
ENV DOCKER_VERSION=1.12.0 \
    DOCKER_DOWNLOAD_SHA256=3dd07f65ea4a7b4c8829f311ab0213bca9ac551b5b24706f3e79a97e22097f8b

RUN apk add gcc libc-dev squashfs-tools make python libarchive-dev tar linux-headers

RUN wget https://github.com/sylabs/singularity/releases/download/2.5.1/singularity-2.5.1.tar.gz && \
    tar xvf singularity-2.5.1.tar.gz && \
    cd singularity-2.5.1 && \
    ./configure --prefix=/usr/local --sysconfdir=/etc && \
    make && \
    make install

ADD images/ /images

RUN apk add iptables

ADD build/distributions/invoker.tar ./

COPY init.sh /
RUN chmod +x init.sh
RUN adduser -D -u ${UID} -h /home/${NOT_ROOT_USER} -s /bin/bash ${NOT_ROOT_USER}

EXPOSE 8080
CMD ["./init.sh", "0"]
