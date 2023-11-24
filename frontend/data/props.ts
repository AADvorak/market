export class DialogConfig {
  constructor(public opened: boolean, public text: string) {}

  static default() {
    return new DialogConfig(false, '')
  }
}

export interface DialogProps {
  config: DialogConfig
}
