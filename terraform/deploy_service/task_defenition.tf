resource "aws_ecs_task_definition" "task_definition" {

  family = var.td_family

  container_definitions = jsonencode([
    {
      name              = var.back_container_name,
      image             = "${data.aws_caller_identity.current_user.account_id}.dkr.ecr.${var.region}.amazonaws.com/${var.back_repository_name}:${var.image_tag}",
      cpu               = 0,
      memory            = 819,
      memoryReservation = 819,
      healthCheck : {
        "command" : ["CMD-SHELL", "curl -f https://${var.subdomain_name}/actuator/health || exit 1"],
        "interval" : 60,
        "timeout" : 5,
        "retries" : 2
      },
      portMappings = [
        {
          name          = "${var.back_container_name}-${var.back_port}-tcp",
          containerPort = var.back_port,
          hostPort      = var.back_port,
          protocol      = "tcp",
          appProtocol   = "http"
        }
      ],
      essential = true,
      environment = [
        {
          name  = "PG_USERNAME",
          value = var.db_username
        },
        {
          name  = "PG_HOST",
          value = data.aws_db_instance.db_host.address
        },
        {
          name  = "PG_PASSWORD",
          value = var.db_password
        },
        {
          name  = "PG_DATABASE",
          value = var.db_name
        },
        {
          name  = "ACTIVE_PROFILE",
          value = var.deploy_profile
        },
        {
          name  = "NEW_RELIC_APP_NAME"
          value = var.new_relic_app_name
        },
        {
          name  = "NEW_RELIC_LICENSE_KEY"
          value = var.new_relic_license_key
        },
        {
          name  = "NEW_RELIC_LOG_LEVEL"
          value = "info"
        }
      ],
      mountPoints = [],
      volumesFrom = [],
      logConfiguration = {
        logDriver = "awslogs",
        options = {
          awslogs-group         = "/ecs/${var.back_container_name}",
          awslogs-create-group  = "true",
          awslogs-region        = var.region,
          awslogs-stream-prefix = "ecs"
        },
        secretOptions = []
      },
      systemControls = []
    }
  ])

  task_role_arn      = data.aws_iam_role.ecs_task_execution_role_arn.arn
  execution_role_arn = data.aws_iam_role.ecs_task_execution_role_arn.arn
  network_mode       = "bridge"
  requires_compatibilities = [
    "EC2"
  ]
  cpu    = "2048"
  memory = "923"
  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

}