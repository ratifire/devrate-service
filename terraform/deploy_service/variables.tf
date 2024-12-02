variable "back_cluster_name" {
  description = "Back cluster name."
  default     = "backend-cluster"
}

variable "back_container_name" {
  description = "Back container name."
  default     = "back-container"
}

variable "back_repository_name" {
  description = "Repository name"
  default     = "backend-service"
}

variable "instance_type" {
  description = "AWS instance type"
  default     = "t3.micro"
}

variable "image_tag" {
  default = "latest"
}

variable "back_port" {
  description = "Port number on which back service is listening"
  default     = 8080
}

variable "subdomain_name" {
  description = "Subdomain name"
  default     = "server.devrate.org"
}

variable "domain_name" {
  description = "Domain name"
  default     = "devrate.org"
}

variable "region" {
  description = "AWS region to host your infrastructure"
  type        = string
}

variable "vpc" {
  default = "vpc-0032e90317069a534"
}

variable "target_group_name" {
  default = "http-ecs-back-tg"
}