# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 复制package文件
COPY package*.json ./

# 配置npm镜像并安装依赖
RUN npm config set registry https://registry.npmmirror.com && \
    npm install

# 复制源码并构建
COPY . .
RUN npm run build

# 生产阶段
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# 复制构建产物
COPY --from=builder /app/dist .

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
