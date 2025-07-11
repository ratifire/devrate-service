variable "max_untagged_images" {
  description = "The maximum number of untagged images to retain in the repository."
  default     = 2
}

variable "region" {
  description = "AWS region to host your infrastructure"
  type        = string
}

variable "instance_type" {
  description = "AWS instance type"
  default     = "t3.micro"
}

variable "repository_name" {
  description = "Repository name"
  default     = "backend-service-dev"
}

variable "list_of_ports" {
  description = "The list of ports the app will use for each other"
  default     = ["22", "80", "3000", "8080", "5432", "443"]
}

variable "cidr_blocks" {
  description = "The list of cidrs to use for each other"
  default     = ["0.0.0.0/0"]
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

variable "db_username" {
  description = "Name of database user"
  default     = "backend"
}

variable "db_password" {
  description = "Password of database"
  default     = "backenddb"
}

variable "s3_bucket_name" {
  description = "Name of the S3 bucket for the backend"
  type        = string
}

variable "backend_security_group_name" {
  default = "Security_group_for_backend_project"
}

variable "back_kay" {
  default = "terraform_ec2_back_key_pair"
}

variable "deploy_profile" {
  default = "dev"
}

variable "matched_participant_name" {
  type = string
}

variable "participant_queue_name" {
  type = string
}

variable "participant_queue_name_dlq" {
  type = string
}
