FROM node:24-alpine AS builder
WORKDIR /app
COPY package.json /
RUN yarn install
COPY . .
RUN yarn run build


FROM node:24-alpine
WORKDIR /app

COPY package.json ./
RUN yarn install --production --frozen-lockfile && yarn cache clean

COPY --from=builder /app/dist ./dist

ENV NODE_ENV=production
ENV TZ="Europe/Berlin"
EXPOSE 4000

CMD ["node", "./dist/frontend/server/server.mjs"]
