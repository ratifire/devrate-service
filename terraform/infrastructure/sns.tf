resource "aws_sns_topic" "meeting_starting_topic" {
  name = var.sns_topic_name
}