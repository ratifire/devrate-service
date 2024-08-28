output "db_endpoint" {
  description = "The db endpoint of the RDS instance"
  value       = data.aws_db_instance.db_host.endpoint
}

output "db_address" {
  description = "The db address of the RDS instance"
  value       = data.aws_db_instance.db_host.address
}

output "ami_id" {
  value = data.aws_ami.aws_linux_latest_ecs.image_id
}

output "ecs_instance_public_ips" {
  description = "Public IP addresses of the filtered instances"
  value       = [for instance in data.aws_instance.filtered_instance_details : instance.public_ip]
}

output "vpc_ids" {
  description = "All VPC ids"
  value       = data.aws_vpcs.all_vpcs.ids
}

output "lb_dns_name" {
  description = "The DNS name of the load balancer."
  value       = data.aws_lb.lb.dns_name
}