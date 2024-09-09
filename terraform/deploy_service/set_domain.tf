
resource "aws_route53_record" "back_a_record" {
  zone_id = data.aws_route53_zone.dns_back_zone.zone_id
  name    = "devrate.org"
  type    = "A"

  alias {
    name                   = aws_lb.back_ecs_alb.dns_name
    zone_id                = aws_lb.back_ecs_alb.zone_id
    evaluate_target_health = true
  }
}


resource "aws_acm_certificate" "devrate_cert" {
  domain_name       = "devrate.org"
  validation_method = "DNS"
}


resource "aws_lb_target_group" "https_ecs_tg" {
  name     = "https-ecs-tg"
  port     = 8080
  protocol = "HTTPS"

  vpc_id = data.aws_vpcs.all_vpcs.ids[0]
  health_check {
    healthy_threshold   = 4
    unhealthy_threshold = 3
    interval            = 60
    protocol            = "HTTPS"
    path                = "/actuator/health"
  }
}

resource "aws_lb_listener" "https_ecs_listener" {
  load_balancer_arn = aws_lb.back_ecs_alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = aws_acm_certificate.devrate_cert.arn
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.https_ecs_tg.arn
  }
}

