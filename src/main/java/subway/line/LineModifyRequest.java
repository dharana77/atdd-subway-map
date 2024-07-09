package subway.line;


public class LineModifyRequest {
  String name;

  String color;

  public LineModifyRequest(){
  }

  public LineModifyRequest(String name, String color) {
    this.name = name;
    this.color = color;
  }
}
