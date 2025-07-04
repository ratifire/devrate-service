resource "aws_sqs_queue" "matcher_queue_dlq" {
  name                       = var.participant_queue_name_dlq
  visibility_timeout_seconds = 30
  delay_seconds              = 0
  message_retention_seconds  = 86400
}

resource "aws_sqs_queue" "participant_queue" {
  name                       = var.participant_queue_name
  visibility_timeout_seconds = 30
  delay_seconds              = 0
  message_retention_seconds  = 86400

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.matcher_queue_dlq.arn
    maxReceiveCount     = 5
  })
}

resource "aws_sqs_queue" "matcher_participant" {
  name                       = var.matched_participant_name
  visibility_timeout_seconds = 30
  delay_seconds              = 0
  message_retention_seconds  = 86400
}

resource "aws_iam_role" "sqs_access_role" {
  name = "SQSAccessRole-${var.deploy_profile}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "sqs_policy" {
  name = "SQSPolicy-${var.deploy_profile}"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["sqs:SendMessage", "sqs:ReceiveMessage"]
        Resource = "*"
      }
    ]
  })
}
