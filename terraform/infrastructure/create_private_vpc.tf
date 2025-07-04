resource "aws_vpc" "skillzzy_main_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "skillzzy-main-vpc"
  }
}

resource "aws_subnet" "private" {
  for_each = var.private_subnets

  vpc_id            = aws_vpc.skillzzy_main_vpc.id
  cidr_block        = each.value
  availability_zone = each.key

  tags = {
    Name = "Private Subnet ${each.key}"
  }
}

resource "aws_subnet" "public" {
  for_each = var.public_subnets

  vpc_id            = aws_vpc.skillzzy_main_vpc.id
  cidr_block        = each.value
  availability_zone = each.key

  map_public_ip_on_launch = true

  tags = {
    Name = "Public Subnet ${each.key}"
  }
}

resource "aws_subnet" "private_db_subnet" {
  for_each = var.private_db_subnets

  vpc_id            = aws_vpc.skillzzy_main_vpc.id
  cidr_block        = each.value
  availability_zone = each.key

  tags = {
    Name = "Private DB Subnet ${each.key}"
  }
}
