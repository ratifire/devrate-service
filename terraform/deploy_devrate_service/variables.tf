variable "region" {
  description = "AWS region to host your infrastructure"
  default     = "eu-north-1"
}

variable "back_cluster_name" {
  description = "Back cluster name"
  default     = "devrate-back-cluster"
}

variable "back_repository_name" {
  description = "Repository name"
  default     = "devrate-backend"
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