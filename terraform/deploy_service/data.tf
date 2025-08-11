
data "aws_caller_identity" "current_user" {}

data "aws_security_group" "vpc_backend_security_group" {
  name = "${var.backend_security_group_name}-${var.deploy_profile}"
}

data "aws_key_pair" "keypair" {
  key_name = var.back_kay
}

data "aws_iam_instance_profile" "aws_iam_instance_profile_backend" {
  name = "ecs-instance-profile-backend-${var.deploy_profile}"
}

data "aws_db_instance" "db_host" {
  db_instance_identifier = "${var.db_instance_identifier}-${var.deploy_profile}"
}

data "aws_subnets" "private_subnets" {
  filter {
    name   = "vpc-id"
    values = [var.vpc]
  }

  filter {
    name   = "tag:Type"
    values = ["private"]
  }

}

data "aws_subnets" "public_subnets" {
  filter {
    name   = "vpc-id"
    values = [var.vpc]
  }

  filter {
    name   = "tag:Type"
    values = ["public"]
  }

}

data "aws_iam_role" "ecs_task_execution_role_arn" {
  name = "ecs-ex-role-backend-${var.deploy_profile}"
}

data "aws_ami" "aws_linux_latest_ecs" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-kernel-5.10-hvm-2.0.20240712-x86_64-ebs"]
  }
}

data "aws_route53_zone" "dns_back_zone" {
  name = var.domain_name
}

data "aws_acm_certificate" "domain_cert" {
  domain   = var.domain_name
  statuses = ["ISSUED"]
  tags = {
    "Name" = var.domain_name
  }
}
