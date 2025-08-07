variable "back_cluster_name" {
  description = "Back cluster name."
  default     = "backend-cluster-dev"
}

variable "back_container_name" {
  description = "Back container name."
  default     = "back-container-dev"
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
  default     = "server.skillzzy.com"
}

variable "domain_name" {
  description = "Domain name"
  default     = "skillzzy.com"
}

variable "region" {
  description = "AWS region to host your infrastructure"
  type        = string
}

variable "vpc" {
  default = "vpc-09bb9f9a748f30da4"
}

variable "target_group_name" {
  default = "http-ecs-back-tg"
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

variable "td_family" {
  description = "Name of TD"
  default     = "backend-td"
}

variable "back_ecs_alb" {
  default = "ecs-back-alb"
}

variable "back_capacity_provider" {
  default = "backend-ec2-capacity-provider"
}

variable "back-ec2-capacity-provider-tag" {
  default = "back-ec2-capacity-provider"
}

variable "ecs_back_launch" {
  default = "ecs_back_launch"
}

variable "backend_security_group_name" {
  default = "Security_group_for_backend_project"
}

variable "back_kay" {
  default = "terraform_ec2_back_key_pair"
}

variable "db_instance_identifier" {
  description = "Name of db_instance_identifier"
  default     = "pg-backend"
}

variable "deploy_profile" {
  default = "dev-private"
}

variable "new_relic_app_name" {
  type        = string
  description = "New Relic application name"
  default     = "skillzzy-new-relic"
}

variable "new_relic_license_key" {
  type        = string
  description = "New Relic license key"
  default     = "not_found"
}

variable "vapid_public_key" {
  description = "VAPID public key"
  type        = string
  sensitive   = true
  default     = "not_found"
}

variable "vapid_private_key" {
  description = "VAPID private key"
  type        = string
  sensitive   = true
  default     = "not_found"
}

variable "vapid_subject" {
  description = "VAPID subject (contact)"
  type        = string
  sensitive   = true
  default     = "not_found"
}