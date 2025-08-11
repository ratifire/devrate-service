resource "aws_ecs_cluster" "backend_cluster" {
  name = var.back_cluster_name
}

resource "aws_launch_template" "ecs_back_launch" {
  name_prefix   = "${var.ecs_back_launch}-${var.deploy_profile}"
  image_id      = data.aws_ami.aws_linux_latest_ecs.image_id
  instance_type = var.instance_type
  # vpc_security_group_ids = [data.aws_security_group.vpc_backend_security_group.id]
  key_name = data.aws_key_pair.keypair.key_name
  user_data = base64encode(<<-EOF
      #!/bin/bash
      echo ECS_CLUSTER=${aws_ecs_cluster.backend_cluster.name} >> /etc/ecs/ecs.config;
    EOF
  )
  iam_instance_profile {
    arn = data.aws_iam_instance_profile.aws_iam_instance_profile_backend.arn
  }

  metadata_options {
    http_tokens                 = "required"
    http_put_response_hop_limit = 2
    http_endpoint               = "enabled"
  }

  block_device_mappings {
    device_name = "/dev/sda1"
    ebs {
      volume_size = 20
      volume_type = "gp2"
    }
  }

  network_interfaces {
    device_index                = 0
    associate_public_ip_address = false
    security_groups             = [data.aws_security_group.vpc_backend_security_group.id]
  }

}

resource "aws_ecs_capacity_provider" "back_capacity_provider" {
  name = "${var.back_capacity_provider}-${var.deploy_profile}"

  auto_scaling_group_provider {
    auto_scaling_group_arn         = aws_autoscaling_group.ecs_back_asg.arn
    managed_termination_protection = "DISABLED"
    managed_scaling {
      maximum_scaling_step_size = 2
      minimum_scaling_step_size = 1
      status                    = "ENABLED"
      target_capacity           = 80
    }
  }

  tags = {
    Name = "${var.back-ec2-capacity-provider-tag}-${var.deploy_profile}"
  }
}

resource "aws_ecs_cluster_capacity_providers" "back_cluster_capacity_provider" {
  cluster_name       = var.back_cluster_name
  capacity_providers = [aws_ecs_capacity_provider.back_capacity_provider.name]

  default_capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.back_capacity_provider.name
    base              = 1
    weight            = 1
  }
}

resource "aws_autoscaling_group" "ecs_back_asg" {
  name = "ASGn-${aws_launch_template.ecs_back_launch.name_prefix}"
  launch_template {
    id      = aws_launch_template.ecs_back_launch.id
    version = aws_launch_template.ecs_back_launch.latest_version
  }
  min_size                  = 2
  max_size                  = 3
  desired_capacity          = 2
  health_check_type         = "EC2"
  health_check_grace_period = 180
  vpc_zone_identifier       = data.aws_subnets.private_subnets.ids
  force_delete              = true
  termination_policies      = ["OldestInstance"]
  initial_lifecycle_hook {
    lifecycle_transition = "autoscaling:EC2_INSTANCE_TERMINATING"
    name                 = "ecs-managed-draining-termination-hook"
    default_result       = "CONTINUE"
    heartbeat_timeout    = 60
  }
  dynamic "tag" {
    for_each = {
      Name  = "Ecs-Back-Instance-ASG-${var.deploy_profile}"
      Owner = "Max Matveichuk"
    }
    content {
      key                 = tag.key
      value               = tag.value
      propagate_at_launch = true
    }
  }
  lifecycle {
    create_before_destroy = true
  }
  protect_from_scale_in = false
  depends_on = [
    aws_launch_template.ecs_back_launch
  ]
}

resource "aws_ecs_service" "back_services" {
  name                               = var.back_repository_name
  cluster                            = var.back_cluster_name
  task_definition                    = aws_ecs_task_definition.task_definition.arn
  scheduling_strategy                = "REPLICA"
  desired_count                      = 2
  force_new_deployment               = true
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  capacity_provider_strategy {
    capacity_provider = aws_ecs_capacity_provider.back_capacity_provider.name
    base              = 1
    weight            = 100
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.http_ecs_back_tg.arn
    container_name   = var.back_container_name
    container_port   = var.back_port
  }
  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_security_group" "alb_sg" {
  name        = "${var.deploy_profile}-alb-sg"
  description = "Security group for ALB"
  vpc_id      = var.vpc

  ingress {
    description = "Allow HTTP from anywhere"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.deploy_profile}-alb-sg"
  }
}
resource "aws_lb" "back_ecs_alb" {
  name               = "${var.back_ecs_alb}-${var.deploy_profile}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = data.aws_subnets.public_subnets.ids

}

resource "aws_lb_target_group" "http_ecs_back_tg" {
  name                 = "${var.target_group_name}-${var.deploy_profile}"
  port                 = var.back_port
  protocol             = "HTTP"
  vpc_id               = var.vpc
  deregistration_delay = "30"

  health_check {
    healthy_threshold   = 2
    unhealthy_threshold = 2
    interval            = 60
    protocol            = "HTTP"
    path                = "/actuator/health"
  }
}

resource "aws_eip" "nat_eip" {
  tags = {
    Name = "nat-eip-${var.deploy_profile}"
  }
}

resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = data.aws_subnets.public_subnets.ids[0]

  tags = {
    Name = "nat-gateway-${var.deploy_profile}"
  }
  depends_on = [data.aws_subnets.public_subnets]
}

resource "aws_route_table" "private_rt" {
  vpc_id = var.vpc
  tags = {
    Name = "private-rt"
    Type = "private"
  }
}

resource "aws_route" "private_nat_route" {
  route_table_id         = aws_route_table.private_rt.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nat_gw.id

  depends_on = [aws_nat_gateway.nat_gw]
}

resource "aws_internet_gateway" "igw" {
  vpc_id = var.vpc
  tags = {
    Name = "igw-${var.deploy_profile}"
  }
}

resource "aws_route_table" "public_rt" {
  vpc_id = var.vpc
  tags = {
    Name = "public-rt"
    Type = "public"
  }
}

resource "aws_route" "public_inet_route" {
  route_table_id         = aws_route_table.public_rt.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.igw.id
}

resource "aws_route_table_association" "public_subnets_assoc" {
  count          = length(data.aws_subnets.public_subnets.ids)
  subnet_id      = data.aws_subnets.public_subnets.ids[count.index]
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table_association" "private_subnets_assoc" {
  count          = length(data.aws_subnets.private_subnets.ids)
  subnet_id      = data.aws_subnets.private_subnets.ids[count.index]
  route_table_id = aws_route_table.private_rt.id
}
