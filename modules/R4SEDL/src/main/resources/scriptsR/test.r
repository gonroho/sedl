library(corrplot)
library(RColorBrewer)
library("ggpubr")
data = read.csv("C:/Users/user/Desktop/examples/dataset2.csv", header=T, sep=";")
mean(data$height)
datagroupby <- aggregate(data$height, by=list(data$sex), FUN=mean)
datagroupby
mean(data[ which(data$sex=='men'),]$height)
sd(data$height)
median(data$height)
mode(data$height)
IQR(data$height)
range(data$height)
t.test(data[ which(data$sex=='men'),]$height,data[ which(data$sex=='women'),]$height,conf.level = 0.95)
aov(height ~ sex, data = data)
pdf(file = "./src/main/resources/resPlots/Plot-1897987982Pearson00.pdf",
    width = 8,
    height = 6) 
M <-cor(data$height, data$weight, method = c("pearson"))
M
ggscatter(data, x = "height", y = "weight", 
          add = "reg.line", conf.int = TRUE, 
          cor.coef = TRUE, cor.method = "pearson",
          xlab = "height", ylab = "weight")
dev.off()
print("Resulting plots saved in PDF format in ./src/main/resources/resPlots")
pdf(file = "./src/main/resources/resPlots/Plot-1897987982Spearman01.pdf",
    width = 8,
    height = 6) 
M <-cor(data$height, data$weight, method = c("spearman"))
M
ggscatter(data, x = "height", y = "weight", 
          add = "reg.line", conf.int = TRUE, 
          cor.coef = TRUE, cor.method = "spearman",
          xlab = "height", ylab = "weight")
dev.off()
print("Resulting plots saved in PDF format in ./src/main/resources/resPlots")
