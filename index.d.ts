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

export namespace Utils {
    export function getVolume(): Promise<number>
    export function getMaxVolume(): Promise<number>
    export function setVolume(value: number, flag?: VOLUME_FLAGS): void
}

export async function initEngine(appkey:string, secret: string, options?: Options): Promise<void>
export function setInitEngineListener(callback: (event: InitEvent) => any): void
export function playText(text: string): void
export function stopPlay(): void
export function release(): void