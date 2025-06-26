
```plantuml
class RobotCommand {
    cmd : ENUM 
}

class WheelSurface {
    surface : ENUM
}

RobotCommand --> WheelSurface
```