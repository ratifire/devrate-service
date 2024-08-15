output "ecs_instance_public_ips" {
  description = "Public IP addresses of the filtered instances"
  value       = [for instance in data.aws_instance.filtered_instance_details : instance.public_ip]
}

output "db_endpoint" {
  description = "The db endpoint of the RDS instance"
  value       = data.aws_db_instance.db_host.endpoint
}

output "db_address" {
  description = "The db address of the RDS instance"
  value       = data.aws_db_instance.db_host.address
}