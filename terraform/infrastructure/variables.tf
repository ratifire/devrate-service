variable "max_untagged_images" {
  description = "The maximum number of untagged images to retain in the repository."
  default     = 2
}

variable "region" {
  description = "AWS region to host your infrastructure"
  default     = "eu-north-1"
}

variable "instance_type" {
  description = "AWS instance type"
  default     = "t3.micro"
}

variable "repository_name" {
  description = "Repository name"
  default     = "backend-service"
}

variable "list_of_ports" {
  description = "The list of ports the app will use for each other"
  default     = ["22", "80", "3000", "8080", "5432", "443"]
}

variable "cidr_blocks" {
  description = "The list of cidrs to use for each other"
  default     = ["10.0.0.0/16"]
}

variable "back_port" {
  description = "Port number on which back service is listening"
  default     = 8080
}

variable "db_instance_identifier" {
  description = "Name of db_instance_identifier"
  default     = "pg-backend"
}

variable "db_name" {
  description = "Name of database"
  default     = "backend"
}
