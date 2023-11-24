export class Price {
  constructor(
      public id: number,
      public shopId: number,
      public shopName: string,
      public price: number) {
  }

  static empty() {
    return new Price(0, 0, '', 0)
  }
}

export class Product {
  constructor(
      public id: number,
      public vendorCode: string,
      public name: string,
      public description: string) {
  }

  static empty() {
    return new Product(0, '', '', '')
  }
}

export class Shop {
  constructor(
      public id: number,
      public name: string,
      public description: string) {
  }

  static empty() {
    return new Shop(0, '', '')
  }
}

export class DataWithCounts<T> {
  constructor(
      public elements: number,
      public pages: number,
      public data: T[]) {
  }

  static empty() {
    return new DataWithCounts(0, 0, [])
  }
}
