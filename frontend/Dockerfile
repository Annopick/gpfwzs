# Runtime only - build is done in GitHub Actions
FROM nginx:alpine

# Copy pre-built dist from GitHub Actions artifact
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]
