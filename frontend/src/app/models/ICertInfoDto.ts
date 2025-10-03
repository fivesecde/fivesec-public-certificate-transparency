export interface ICertInfoDto {
  domain: string;
  id: string;
  issuerDn: string;
  notAfter: string;
  notBefore: string;
  serialHex: string;
  subjectDn: string;
}

export interface ICertRawInfoEntry {
  extra_data: string,
  leaf_input: string,
}
