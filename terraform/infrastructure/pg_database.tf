resource "aws_db_instance" "pg_db_backend" {
  engine              = "postgres"
  engine_version      = "16.8"
  instance_class      = "db.${var.instance_type}"
  storage_type        = "gp2"
  allocated_storage   = 20
  db_name             = var.db_name
  username            = var.db_username
  password            = var.db_password
  publicly_accessible = true
  skip_final_snapshot = true
  identifier          = var.db_instance_identifier

  vpc_security_group_ids = [aws_security_group.backend_security_group.id]
}
