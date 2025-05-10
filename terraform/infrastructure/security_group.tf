resource "aws_security_group" "backend_security_group" {
  name        = var.backend_security_group_name
  description = "Allow tcp inbound traffic and all outbound traffic"
  vpc_id      = aws_vpc.main_vpc.id

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


resource "aws_subnet" "private_az1" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = data.aws_availability_zones.availability_zones.names[0]
  map_public_ip_on_launch = false
  tags = {
    Name = "private-subnet-az1"
  }
}

resource "aws_subnet" "private_az2" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = data.aws_availability_zones.availability_zones.names[1]
  map_public_ip_on_launch = false
  tags = {
    Name = "private-subnet-az2"
  }
}

resource "aws_subnet" "private_az3" {
  vpc_id                  = aws_vpc.main_vpc.id
  cidr_block              = "10.0.3.0/24"
  availability_zone       = data.aws_availability_zones.availability_zones.names[2]
  map_public_ip_on_launch = false
  tags = {
    Name = "private-subnet-az3"
  }
}
