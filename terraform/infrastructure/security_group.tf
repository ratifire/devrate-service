resource "aws_security_group" "backend_security_group" {
  name        = "Security_group_for_backend_project"
  description = "Allow tcp inbound traffic and all outbound traffic"
  vpc_id      = aws_default_vpc.default_vpc.id

  dynamic "ingress" {
    for_each = var.list_of_ports
    content {
      protocol    = "tcp"
      from_port   = ingress.value
      to_port     = ingress.value
      cidr_blocks = var.cidr_blocks
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = var.cidr_blocks
  }
}

resource "aws_default_subnet" "default_az1" {
  availability_zone = data.aws_availability_zones.availability_zones.names[0]

  tags = {
    Name = "Default subnet for ${data.aws_availability_zones.availability_zones.names[0]}"
  }
}

resource "aws_default_subnet" "default_az2" {
  availability_zone = data.aws_availability_zones.availability_zones.names[1]

  tags = {
    Name = "Default subnet for ${data.aws_availability_zones.availability_zones.names[1]}"
  }
}

resource "aws_default_subnet" "default_az3" {
  availability_zone = data.aws_availability_zones.availability_zones.names[2]

  tags = {
    Name = "Default subnet for ${data.aws_availability_zones.availability_zones.names[2]}"
  }
}
