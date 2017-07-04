package util;

import com.ClientSendMessage;
import com.ServerResponseMessage;

/**
 * Created by XHWhy on 2017/7/4.
 */
public class WhiteBoardConvert {
    public static ServerResponseMessage.WhiteBoardMessage
        client2server(ClientSendMessage.WhiteBoardMessage request) {
        ServerResponseMessage.WhiteBoardMessage.Builder builder =
                ServerResponseMessage.WhiteBoardMessage.newBuilder()
                        .setColor(request.getColor())
                        .setPensize(request.getPensize())
                        .setX1(request.getX1())
                        .setY1(request.getY1())
                        .setX2(request.getX2())
                        .setY2(request.getY2())
                        .setQuestionId(request.getQuestionId())
                        .setIsCls(request.getIsCls())
                        .setIsACls(request.getIsACls())
                        .setIsReceiveImage(request.getIsReceiveImage());

        if(request.getIsReceiveImage()) {
            ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint.Builder pointBuilder =
                    ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint.newBuilder();

            ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect.Builder rectBuilder =
                    ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect.newBuilder();

            ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.Builder imageBuilder =
                    ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.newBuilder();

            for (ClientSendMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint point : request.getImage().getPointsList()) {
                pointBuilder.setX1(point.getX1()).setY1(point.getY1()).setX2(point.getX2()).setY2(point.getY2())
                        .setColor(point.getColor()).setPensize(point.getPensize());
                imageBuilder.addPoints(pointBuilder);
            }
            for (ClientSendMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect rect : request.getImage().getRectsList()) {
                rectBuilder.setX1(rect.getX1()).setY1(rect.getY1()).setX2(rect.getX2()).setY2(rect.getY2());
                imageBuilder.addRects(rectBuilder);
            }

            builder.setImage(imageBuilder);
        }

        return builder.build();
    }

    public static ServerResponseMessage.WhiteBoardMessage
        server2client(GraphicPoints image) {
        ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint.Builder pointBuilder =
                ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicPoint.newBuilder();

        ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect.Builder rectBuilder =
                ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.GraphicRect.newBuilder();

        ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.Builder imageBuilder =
                ServerResponseMessage.WhiteBoardMessage.WhiteBoardImage.newBuilder();

        for (GraphicPoints.GraphicPoint point : image.points) {
            pointBuilder.setX1(point.x1).setY1(point.y1).setX2(point.x2).setY2(point.y2)
                    .setColor(point.color.getRGB()).setPensize(point.pensize);
            imageBuilder.addPoints(pointBuilder);
        }
        for (GraphicPoints.GraphicClearRect rect : image.rects) {
            rectBuilder.setX1(rect.x1).setY1(rect.y1).setX2(rect.x2).setY2(rect.y2);
            imageBuilder.addRects(rectBuilder);
        }

        ServerResponseMessage.WhiteBoardMessage message = ServerResponseMessage.WhiteBoardMessage.newBuilder()
                .setIsReceiveImage(true)
                .setImage(imageBuilder).build();
        return  message;
    }
}
