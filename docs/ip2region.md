# IP2Region 数据库文件

## 下载说明

请从以下地址下载最新的 `ip2region.xdb` 数据库文件，并放置在此目录下：

**下载地址：**
https://github.com/lionsoul2014/ip2region/tree/master/data

**步骤：**
1. 访问上述 GitHub 链接
2. 下载 `ip2region.xdb` 文件
3. 将文件放置到 `origin-admin/src/main/resources/ip2region/` 目录下

## 文件说明

- **ip2region.xdb**: IP 地址数据库文件（二进制格式）
- 文件大小约 11MB
- 包含全球 IP 地址归属地信息
- 支持离线查询，查询速度极快

## 注意事项

- 该文件不应提交到 Git 仓库（已在 .gitignore 中忽略）
- 建议定期更新数据库文件以获取最新的 IP 归属地数据
- 如果文件缺失，IP 地址解析将返回"未知"
