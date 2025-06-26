# syntax=docker/dockerfile:1
FROM php:8.2-fpm

# Install system dependencies
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libicu-dev \
    libpq-dev \
    libzip-dev \
    zip \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    nginx \
    && docker-php-ext-configure intl \
    && docker-php-ext-install intl pdo pdo_mysql zip gd

# Install Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy project files
COPY . .

# Install PHP dependencies
RUN composer install --no-interaction --optimize-autoloader --no-dev

# Copy nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start PHP-FPM and Nginx
CMD service nginx start && php-fpm
