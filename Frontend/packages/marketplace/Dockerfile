FROM node:22-alpine

WORKDIR /app

COPY .next/standalone/packages/marketplace ./
COPY .next/standalone/node_modules ./node_modules
COPY .next/static ./.next/static
COPY public ./public

EXPOSE 3000

CMD ["node", "server.js"]