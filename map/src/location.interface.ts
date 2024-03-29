export interface LocationData {
    event_time: string;
    time: string;
    lon: number;
    lat: number;
    height: number;
    valid: number;
    sat_cnt: number;
    spd: number;
    dir: number;
    race: number;
}

export interface LocationEvent {
    id: number;
    _s: string;
    _w: boolean;
    _t: boolean;
    data: LocationData;
}

export function getAngle(angle: number): number {
    if (angle < 0) {
        return NaN;
    }
    return 10 * (angle % 10 <= 5 ? Math.floor(angle / 10) : Math.ceil(angle / 10));
}