import styled from "styled-components";

import GammaTextField from "../../../../common/elements/gamma-text-field";
import GammaSelect from "../../../../common/elements/gamma-select";

import { DigitButton, DigitSwitch } from "@cthit/react-digit-components";

export const CreateAccountButton = styled(DigitButton)``;

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

export const AcceptanceYearInput = styled(GammaSelect)`
    width: 300px;
`;

export const AcceptUserAgreementInput = styled(DigitSwitch)``;
