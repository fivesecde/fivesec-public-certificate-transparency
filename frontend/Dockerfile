FROM node:24-alpine AS builder
RUN mkdir /app
COPY . /app
WORKDIR /app

ENV API_KEY=''
ENV BACKEND_URL=''
RUN npm install && npm run build


FROM node:24-alpine
WORKDIR /app
COPY --from=builder /app/dist ./dist
ENV TZ="Europe/Berlin"
EXPOSE 4000
CMD ["node", "./dist/frontend/server/server.mjs"]
