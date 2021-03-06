export enum INIT_ENGINE_STATE {
    START,
    PROGRESS,
    FINISH,
    SDK_NOT_FOUND_ERROR,
    SDK_COPY_ERROR
}

export enum STREAM_TYPE {
    SYSTEM,
    RING,
    MUSIC,
    ALARM,
    NOTIFICATION
}

export enum VOLUME_FLAGS {
    ALLOW_RINGER_MODES,
    PLAY_SOUND,
    REMOVE_SOUND_AND_VIBRATE,
    SHOW_UI,
    VIBRATE
}

export interface Options {
    streamType: STREAM_TYPE,
    volume: number,
    pitch: number,
    speed: number,
}

export interface InitEvent {
    progress: number, 
    fileCount: number, 
    initState: INIT_ENGINE_STATE
}

export interface ErrorEvent {
    msg: string
}

export namespace utils {
    export function getVolume(): Promise<number>
    export function getMaxVolume(): Promise<number>
    export function setVolume(value: number, flag?: VOLUME_FLAGS): void
}

export function initEngine(appkey:string, secret: string, options?: Options): Promise<void>

export function setInitEngineListener(callback: (event: InitEvent) => any): void
export function setPlayStartListener(callback: () => any): void
export function setPlayEndListener(callback: () => any): void
export function setPauseListener(callback: () => any): void
export function setStopListener(callback: () => any): void
export function setResumeListener(callback: () => any): void
export function setReleaseListener(callback: () => any): void
export function setErrorListener(callback: (event: ErrorEvent) => any): void

export function playText(text: string): void
export function stopPlay(): void
export function pause(): void
export function resume(): void
export function release(): void