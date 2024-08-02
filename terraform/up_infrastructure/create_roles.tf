locals {
  policies_for_ex = [
    "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
    "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess",
    "arn:aws:iam::aws:policy/CloudWatchFullAccess",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/ElasticLoadBalancingFullAccess",
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
  ]

  policies_for_inst = [
    "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess",
    "arn:aws:iam::aws:policy/AmazonRDSFullAccess",
    "arn:aws:iam::aws:policy/AutoScalingFullAccess",
  ]
}

resource "aws_iam_role" "ecs_ex_role" {
  name = "ecs-ex-role"
  assume_role_policy = jsonencode({
    "Version" : "2008-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_policy_attachments_ex_role" {
  count = length(local.policies_for_ex)

  role       = aws_iam_role.ecs_ex_role.name
  policy_arn = local.policies_for_ex[count.index]
}

resource "aws_iam_role" "ecs_inst_role" {
  name = "ecs-inst-role"
  assume_role_policy = jsonencode({
    "Version" : "2008-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "ec2.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}


resource "aws_iam_role_policy_attachment" "ecs_policy_attachments_inst_role" {
  count = length(local.policies_for_inst)

  role       = aws_iam_role.ecs_inst_role.name
  policy_arn = local.policies_for_inst[count.index]
}

resource "aws_iam_instance_profile" "instance_profile_dr" {
  name = "new-ecs-instance-profile-dr"
  role = aws_iam_role.ecs_inst_role.name
}