resource "aws_db_instance" "pg_db_backend" {
  engine              = "postgres"
  engine_version      = "16.3"
  instance_class      = "db.${var.instance_type}"
  storage_type        = "gp2"
  allocated_storage   = 20
  db_name             = var.db_name
  username            = "backend"
  password            = "backenddb"
  publicly_accessible = true
  skip_final_snapshot = true
  identifier          = var.db_instance_identifier

  vpc_security_group_ids = [aws_security_group.backend_security_group.id]
  db_subnet_group_name   = aws_db_subnet_group.private_db_subnet_group.name

}

resource "aws_db_subnet_group" "private_db_subnet_group" {
  name       = "private-db-subnet-group"
  subnet_ids = [aws_subnet.private_subnet_db.id]

  tags = {
    Name = "Private DB Subnet Group"
  }
}
