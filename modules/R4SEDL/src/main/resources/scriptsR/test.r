data = read.csv("C:/Users/user/Desktop/examples/dataset.csv", header=T, sep=";")
mean(data$height)
datagroupby <- aggregate(data$height, by=list(data$sex), FUN=mean)
datagroupby
mean(data[ which(data$sex=='men'),]$height)
sd(data$height)
