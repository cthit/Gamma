import styled from "styled-components";

import GammaTextField from "../../../../common/elements/gamma-text-field";

import { DigitButton, DigitCheckbox } from "@cthit/react-digit-components";

export const CIDInput = styled(GammaTextField)`
    width: 280p;
`;
export const PasswordInput = styled(GammaTextField)`
    width: 280px;
`;

export const CreateAccountButton = styled(DigitButton)``;
export const LoginButton = styled(DigitButton)``;

export const RememberMe = styled(DigitCheckbox)``;
