enum PlatformStatus {
    NO_TASK = 0,
    CLEANED = 10,
    CLEANED_TIMEOUT = 11,
    PENDING = 20,
    NOT_VISITED = 30,
    NOT_CLEANED = 31
}

export function getColor(status: number): string {
    switch (status) {
        case PlatformStatus.CLEANED:
            return "#56B60B";
        case PlatformStatus.CLEANED_TIMEOUT:
            return "#56B60B";
        case PlatformStatus.PENDING:
            return "#FFD600";
        case PlatformStatus.NOT_VISITED:
            return "#FF1313";
        case PlatformStatus.NOT_CLEANED:
            return "#FF8A00";
        default:
            return "#c4c4c4";
    }
}