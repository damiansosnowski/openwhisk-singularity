# Licensed to the Apache Software Foundation (ASF) under one or more contributor
# license agreements; and to You under the Apache License, Version 2.0.

FROM scala

ENV UID=1001 \
    NOT_ROOT_USER=owuser
ENV DOCKER_VERSION=1.12.0 \
    DOCKER_DOWNLOAD_SHA256=3dd07f65ea4a7b4c8829f311ab0213bca9ac551b5b24706f3e79a97e22097f8b

RUN apk add --update openssl

RUN export VERSION=1.11 OS=linux ARCH=amd64 && \
    wget https://dl.google.com/go/go$VERSION.$OS-$ARCH.tar.gz && \
    tar -C /usr/local -xzf go$VERSION.$OS-$ARCH.tar.gz && \
    rm go$VERSION.$OS-$ARCH.tar.gz

RUN apk add git && \
    echo 'export GOPATH=${HOME}/go' >> ~/.bashrc && \
    echo 'export PATH=/usr/local/go/bin:${PATH}:${GOPATH}/bin' >> ~/.bashrc

RUN apk add gcc libc-dev squashfs-tools make python libarchive-dev tar linux-headers libseccomp-dev pkgconfig openssl-dev util-linux-dev 

RUN source ~/.bashrc && \
    go get -u github.com/golang/dep/cmd/dep && \
    mkdir -p $GOPATH/src/github.com/sylabs && \
    cd $GOPATH/src/github.com/sylabs && \
    wget https://github.com/sylabs/singularity/releases/download/v3.1.1/singularity-3.1.1.tar.gz && \
    tar xf singularity-3.1.1.tar.gz && \
    cd singularity && \
    ./mconfig && \
    make -C ./builddir && \
    make -C ./builddir install

COPY nodejs6action.sif /

RUN apk add iptables

ADD build/distributions/invoker.tar ./

COPY init.sh /
RUN chmod +x init.sh
RUN adduser -D -u ${UID} -h /home/${NOT_ROOT_USER} -s /bin/bash ${NOT_ROOT_USER}

EXPOSE 8080
CMD ["./init.sh", "0"]

