output "db_endpoint" {
  description = "The db endpoint of the RDS instance"
  value       = data.aws_db_instance.db_host.endpoint
}

output "db_address" {
  description = "The db address of the RDS instance"
  value       = data.aws_db_instance.db_host.address
}