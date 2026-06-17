FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y --no-install-recommends \
    nginx \
    supervisor \
    openssh-server \
    ca-certificates \
    curl \
    php \
    && rm -rf /var/lib/apt/lists/*
    

RUN mkdir -p /var/log/supervisor /var/run/sshd /app

RUN echo "root:Hello@123" | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

COPY web/nginx.conf /etc/nginx/nginx.conf
COPY web/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY web/app.war /app/app.war

EXPOSE 8080 22

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
