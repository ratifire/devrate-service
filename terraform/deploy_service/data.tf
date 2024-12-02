data "aws_region" "current_region" {}

data "aws_caller_identity" "current_user" {}

data "aws_availability_zones" "availability_zones" {}

data "aws_security_group" "vpc_backend_security_group" {
  name = "Security_group_for_backend_project"
}

data "aws_key_pair" "keypair" {
  key_name = "terraform_ec2_back_key_pair"
}

data "aws_iam_instance_profile" "aws_iam_instance_profile_backend" {
  name = "ecs-instance-profile-backend"
}

data "aws_vpcs" "all_vpcs" {}


data "aws_db_instance" "db_host" {
  db_instance_identifier = "pg-backend"
}

data "aws_subnets" "default_subnets" {
  filter {
    name   = "vpc-id"
    values = [var.vpc]
  }


  filter {
    name = "tag:Name"
    values = [
      "Default subnet for eu-north-1b", "Default subnet for eu-north-1a",
      "Default subnet for eu-north-1c"
    ]
  }

}

data "aws_iam_role" "ecs_task_execution_role_arn" {
  name = "ecs-ex-role-backend"
}
data "aws_iam_role" "ecs_instance_role" {
  name = "ecs-inst-role-backend"
}

data "aws_ami" "aws_linux_latest_ecs" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-kernel-5.10-hvm-2.0.20240712-x86_64-ebs"]
  }
}

data "aws_instances" "filtered_instances" {
  filter {
    name   = "tag:Name"
    values = ["Ecs-Back-Instance-ASG"]
  }
}

data "aws_instance" "filtered_instance_details" {
  for_each    = toset(data.aws_instances.filtered_instances.ids)
  instance_id = each.value
}

data "aws_lb" "lb" {
  name       = aws_lb.back_ecs_alb.name
  depends_on = [aws_lb.back_ecs_alb]
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
