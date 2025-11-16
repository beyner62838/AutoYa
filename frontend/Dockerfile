FROM node:18 AS build
WORKDIR /app

ARG VITE_API_URL=http://localhost:10080
ENV VITE_API_URL=${VITE_API_URL}

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:18-alpine AS runtime
WORKDIR /app

RUN npm install -g http-server
COPY --from=build /app/dist ./dist

EXPOSE 80
CMD ["http-server", "dist", "-p", "80", "-a", "0.0.0.0"]


