import styled from "styled-components";
import GammaSwitch from "../../../../common/views/gamma-switch";
import GammaTextField from "../../../../common/views/gamma-text-field";
import GammaSelect from "../../../../common/views/gamma-select";
import GammaButton from "../../../../common/views/gamma-button";

export const CreateAccountButton = styled(GammaButton)``;

export const ConfirmationCodeInput = styled(GammaTextField)`
  width: 100px;
`;

export const NickInput = styled(GammaTextField)`
  width: 300px;
`;

export const PasswordInput = styled(GammaTextField)`
  width: 300px;
`;

export const PasswordConfirmationInput = styled(GammaTextField)`
  width: 300px;
`;

export const FirstnameInput = styled(GammaTextField)`
  width: 300px;
`;

export const LastnameInput = styled(GammaTextField)`
  width: 300px;
`;

export const ConfirmCidInput = styled(GammaTextField)``;

export const AttendanceYearInput = styled(GammaSelect)`
  width: 300px;
`;

export const AcceptUserAgreementInput = styled(GammaSwitch)``;
