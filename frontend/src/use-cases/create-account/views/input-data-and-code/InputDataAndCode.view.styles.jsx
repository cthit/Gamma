import styled from "styled-components";

import GammaSelect from "../../../../common/elements/gamma-select";

import {
    DigitButton,
    DigitSwitch,
    DigitTextField
} from "@cthit/react-digit-components";

export const CreateAccountButton = styled(DigitButton)``;

export const ConfirmationCodeInput = styled(DigitTextField)`
    width: 100px;
`;

export const NickInput = styled(DigitTextField)`
    width: 300px;
`;

export const PasswordInput = styled(DigitTextField)`
    width: 300px;
`;

export const PasswordConfirmationInput = styled(DigitTextField)`
    width: 300px;
`;

export const FirstnameInput = styled(DigitTextField)`
    width: 300px;
`;

export const LastnameInput = styled(DigitTextField)`
    width: 300px;
`;

export const ConfirmCidInput = styled(DigitTextField)``;

export const AcceptanceYearInput = styled(GammaSelect)`
    width: 300px;
`;

export const AcceptUserAgreementInput = styled(DigitSwitch)``;
