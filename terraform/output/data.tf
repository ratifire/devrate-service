data "aws_region" "current_region" {}

data "aws_caller_identity" "current_user" {}

data "aws_db_instance" "db_host" {
  db_instance_identifier = "pg-backend"
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