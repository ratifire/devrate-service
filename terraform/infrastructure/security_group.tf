resource "aws_security_group" "backend_security_group" {
  name        = var.backend_security_group_name
  description = "Allow tcp inbound traffic and all outbound traffic"
  vpc_id      = aws_vpc.skillzzy_main_vpc.id

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
