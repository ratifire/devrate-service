resource "aws_security_group" "backend_security_group" {
  name        = var.backend_security_group_name
  description = "Allow tcp inbound traffic and all outbound traffic"
  vpc_id      = var.main_vpc_id

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


resource "aws_subnet" "public_subnet" {
  count                   = 3
  vpc_id                  = local.vpc_id
  availability_zone       = local.azs[count.index]
  cidr_block              = cidrsubnet("10.0.0.0/16", 8, count.index)
  map_public_ip_on_launch = true

  tags = {
    Name = "public-subnet-${count.index + 1}"
    Type = "public"
  }
}

resource "aws_subnet" "private_subnet" {
  count             = 3
  vpc_id            = local.vpc_id
  availability_zone = local.azs[count.index]
  cidr_block        = cidrsubnet("10.1.0.0/16", 8, count.index)

  tags = {
    Name = "private-subnet-${var.deploy_profile}-${count.index + 1}"
    Type = "private"
  }
}