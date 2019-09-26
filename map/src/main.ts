const script = document.createElement('script');
script.src = "https://api-maps.yandex.ru/2.1/?apikey=a92bd5d3-176f-40b5-8213-844995d832ab&lang=ru_RU";
script.type = "text/javascript";
script.async = false;
script.onload = function () {
    ymaps.ready(init);
};
document.getElementsByTagName('head')[0].appendChild(script);

function init() {
    const map = new ymaps.Map("map", {
        center: [55.45, 37.36],
        zoom: 8,
        controls: []
    }, {
        autoFitToViewport: "always",
        suppressMapOpenBlock: true
    });
}
/*const defaultBehavior = ["drag", "scrollZoom", "dblClickZoom", "multiTouch", "rightMouseButtonMagnifier"];
const element: HTMLDivElement = document.createElement("div");
const map = new ymaps.Map(
    element,
    {
        behaviors: defaultBehavior,
        center:    [55.76, 37.64],
        controls:  [],
        type:      "yandex#map",
        zoom:      10
    },
    {
        autoFitToViewport: "always"
    });

map.behaviors.disable(defaultBehavior);

map.events.add("click", () => {
});

const balloonLayout = ymaps.templateLayoutFactory.createClass(
    "<div class=\"map-marker-balloon\"></div>",
    {
        build(this: ymaps.ILayout): void {
            ((this.constructor as any).superclass as ymaps.layout.templateBased.Base).build.call(this);
            this.getParentElement().children.item(0)!.children.item(0)!.appendChild((this.getData() as any).properties.get("balloonContent"));
        }
    }
);

const mapMarker = new ymaps.Placemark(
    [55.76, 37.64],
    {
        balloonContent:         "test",
        balloonAutoPan:         true,
        balloonLayout,
        balloonPanelMaxMapArea: 0,
        iconLayout:             "default#image",
        iconImageHref:          "./test/icon.png",
        iconImageSize:          [26, 26],
        iconImageOffset:        [13, 13],
        hasBalloon:             true,
        hasHint:                false,
        hideIconOnBalloonOpen:  true,
        openBalloonOnClick:     false,
        zIndex:                 1
    }
);

mapMarker.events.add("click", (event: ymaps.Event) => {
});

map.geoObjects.add(mapMarker);

map.setCenter((mapMarker.geometry as ymaps.IPointGeometry).getCoordinates() || [55.76, 37.64]);

map.layers.each((layer) => {
    if (layer.getBrightness) {
        layer.getBrightness();
    }
});

map.setZoom(13);*/