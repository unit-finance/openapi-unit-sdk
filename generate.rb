require 'json'
require 'open3'

# Function to replace paths in the OpenAPI JSON file
def replace_paths_with_one_of(openapi_file, new_paths)
  begin
    openapi_data = JSON.parse(File.read(openapi_file))

    # Replace the 'paths' section with new paths
    openapi_data['paths'] = new_paths

    # Write the modified OpenAPI file back
    File.open(openapi_file, 'w') do |file|
      file.write(JSON.pretty_generate(openapi_data))
    end

    puts "OpenAPI file updated successfully."
  rescue => e
    puts "An error occurred: #{e.message}"
  end
end

# New paths referencing 'schemas_with_one_of'
new_paths = {
  "/applications/{applicationId}": {
    "$ref": "./schemas_with_one_of/application_paths.json#/application"
  },
  "/applications/{applicationId}/documents/{documentId}": {
    "$ref": "./schemas_with_one_of/application_paths.json#/upload_pdf_file"
  },
  "/applications/{applicationId}/documents/{documentId}/": {
    "$ref": "./schemas_with_one_of/application_paths.json#/upload_png_file"
  },
  "/applications/{applicationId}/documents/{documentId}/?": {
    "$ref": "./schemas_with_one_of/application_paths.json#/upload_jpg_file"
  },
  "/applications/{applicationId}/cancel": {
    "$ref": "./schemas_with_one_of/application_paths.json#/cancel_application"
  },
  "/applications": {
    "$ref": "./schemas_with_one_of/application_paths.json#/applications"
  },
  "/application-forms": {
    "$ref": "./schemas_with_one_of/application_paths.json#/application_forms"
  },
  "/application-forms/{applicationFormId}": {
    "$ref": "./schemas_with_one_of/application_paths.json#/application_form"
  },
  "/accounts": {
    "$ref": "./schemas_with_one_of/account_paths.json#/accounts"
  },
  "/accounts/{accountId}": {
    "$ref": "./schemas_with_one_of/account_paths.json#/account"
  },
  "/accounts/{accountId}/limits": {
    "$ref": "./schemas_with_one_of/account_paths.json#/get_account_limits"
  },
  "/accounts/{accountId}/unfreeze": {
    "$ref": "./schemas_with_one_of/account_paths.json#/unfreeze_account"
  },
  "/accounts/{accountId}/freeze": {
    "$ref": "./schemas_with_one_of/account_paths.json#/freeze_account"
  },
  "/accounts/{accountId}/close": {
    "$ref": "./schemas_with_one_of/account_paths.json#/close_account"
  },
  "/accounts/{accountId}/reopen": {
    "$ref": "./schemas_with_one_of/account_paths.json#/reopen_account"
  },
  "/accounts/{accountId}/enter-daca": {
    "$ref": "./schemas_with_one_of/account_paths.json#/enter_daca"
  },
  "/accounts/{accountId}/activate-daca": {
    "$ref": "./schemas_with_one_of/account_paths.json#/activate_daca"
  },
  "/accounts/{accountId}/deactivate-daca": {
    "$ref": "./schemas_with_one_of/account_paths.json#/deactivate_daca"
  },
  "/account-end-of-day": {
    "$ref": "./schemas_with_one_of/account_paths.json#/get_account_end_of_day"
  },
  "/customers/{customerId}": {
    "$ref": "./schemas_with_one_of/customer_paths.json#/customer"
  },
  "/customers": {
    "$ref": "./schemas_with_one_of/customer_paths.json#/list_customers"
  },
  "/customers/{customerId}/archive": {
    "$ref": "./schemas_with_one_of/customer_paths.json#/archive_customer"
  },
  "/payments": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/get_and_create_payments"
  },
  "/payments/{paymentId}": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/get_and_patch_payment"
  },
  "/payments/{paymentId}/cancel": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/cancel_payment"
  },
  "/received-payments": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/list_received_payments"
  },
  "/received-payments/{paymentId}": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/received_payments"
  },
  "/received-payments/{paymentId}/advance": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/advance_received_payment"
  },
  "/counterparties": {
    "$ref": "./schemas_with_one_of/counterparty_paths.json#/counterparties"
  },
  "/counterparties/{counterpartyId}": {
    "$ref": "./schemas_with_one_of/counterparty_paths.json#/counterparty"
  },
  "/counterparties/{counterpartyId}/balance": {
    "$ref": "./schemas_with_one_of/counterparty_paths.json#/counterparty_balance"
  },
  "/recurring-payments": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/recurring_payments"
  },
  "/recurring-payments/{paymentId}": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/get_recurring_payment"
  },
  "/recurring-payments/{paymentId}/disable": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/disable_recurring_payment"
  },
  "/recurring-payments/{paymentId}/enable": {
    "$ref": "./schemas_with_one_of/payments_paths.json#/enable_recurring_payment"
  },
  "/cards": {
    "$ref": "./schemas_with_one_of/card_paths.json#/cards"
  },
  "/cards/{cardId}": {
    "$ref": "./schemas_with_one_of/card_paths.json#/card"
  },
  "/cards/{cardId}/secure-data/pin/status": {
    "$ref": "./schemas_with_one_of/card_paths.json#/card_pin_status"
  },
  "/cards/{cardId}/report-stolen": {
    "$ref": "./schemas_with_one_of/card_paths.json#/report_stolen_card"
  },
  "/cards/{cardId}/report-lost": {
    "$ref": "./schemas_with_one_of/card_paths.json#/report_lost_card"
  },
  "/cards/{cardId}/close": {
    "$ref": "./schemas_with_one_of/card_paths.json#/close_card"
  },
  "/cards/{cardId}/freeze": {
    "$ref": "./schemas_with_one_of/card_paths.json#/freeze_card"
  },
  "/cards/{cardId}/unfreeze": {
    "$ref": "./schemas_with_one_of/card_paths.json#/unfreeze_card"
  },
  "/cards/{cardId}/limits": {
    "$ref": "./schemas_with_one_of/card_paths.json#/card_limits"
  },
  "/authorizations": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/authorizations"
  },
  "/authorizations/{authorizationId}": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/get_authorization"
  },
  "/authorization-requests": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/get_authorizations_requests"
  },
  "/authorization-requests/{authorizationId}": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/get_authorization_requests"
  },
  "/authorization-requests/{authorizationId}/approve": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/approve_authorization_requests"
  },
  "/authorization-requests/{authorizationId}/decline": {
    "$ref": "./schemas_with_one_of/authorization_paths.json#/decline_authorization_requests"
  },
  "/statements": {
    "$ref": "./schemas_with_one_of/statement_paths.json#/get_statements"
  },
  "/statements/{statementId}/html": {
    "$ref": "./schemas_with_one_of/statement_paths.json#/get_statement_html"
  },
  "/statements/{statementId}/pdf": {
    "$ref": "./schemas_with_one_of/statement_paths.json#/get_statement_pdf"
  },
  "/statements/{accountId}/bank/pdf": {
    "$ref": "./schemas_with_one_of/statement_paths.json#/get_statement_bank_pdf"
  },
  "/rewards": {
    "$ref": "./schemas_with_one_of/reward_paths.json#/rewards"
  },
  "/rewards/{rewardId}": {
    "$ref": "./schemas_with_one_of/reward_paths.json#/get_reward"
  },
  "/events": {
    "$ref": "./schemas_with_one_of/event_paths.json#/list_events"
  },
  "/events/{eventId}": {
    "$ref": "./schemas_with_one_of/event_paths.json#/event"
  },
  "/institutions/{routingNumber}": {
    "$ref": "./schemas_with_one_of/institution_paths.json#/get_institution"
  },
  "/fees": {
    "$ref": "./schemas_with_one_of/fee_paths.json#/create_fee"
  },
  "/check-deposits": {
    "$ref": "./schemas_with_one_of/check_deposit_paths.json#/check_deposits"
  },
  "/check-deposits/{checkDepositId}": {
    "$ref": "./schemas_with_one_of/check_deposit_paths.json#/check_deposit"
  },
  "/check-deposits/{checkDepositId}/confirm": {
    "$ref": "./schemas_with_one_of/check_deposit_paths.json#/confirm_check_deposit"
  },
  "/check-deposits/{checkDepositId}/front": {
    "$ref": "./schemas_with_one_of/check_deposit_paths.json#/get_front_check_deposit"
  },
  "/check-deposits/{checkDepositId}/back": {
    "$ref": "./schemas_with_one_of/check_deposit_paths.json#/get_back_check_deposit"
  },
  "/users/{userId}/api-tokens": {
    "$ref": "./schemas_with_one_of/token_paths.json#/create_token"
  },
  "/users": {
    "$ref": "./schemas_with_one_of/token_paths.json#/list_tokens"
  },
  "/users/{userId}/api-tokens/{tokenId}": {
    "$ref": "./schemas_with_one_of/token_paths.json#/delete_token"
  },
  "/customers/{customerId}/token": {
    "$ref": "./schemas_with_one_of/token_paths.json#/create_customer_token"
  },
  "/customers/{customerId}/token/verification": {
    "$ref": "./schemas_with_one_of/token_paths.json#/create_customer_token_verification"
  },
  "/webhooks": {
    "$ref": "./schemas_with_one_of/webhook_paths.json#/webhooks"
  },
  "/webhooks/{webhookId}": {
    "$ref": "./schemas_with_one_of/webhook_paths.json#/webhook"
  },
  "/webhooks/{webhookId}/enable": {
    "$ref": "./schemas_with_one_of/webhook_paths.json#/enable_webhook"
  },
  "/webhooks/{webhookId}/disable": {
    "$ref": "./schemas_with_one_of/webhook_paths.json#/disable_webhook"
  },
  "/atm-locations": {
    "$ref": "./schemas_with_one_of/atm_location_paths.json#/list_atm_locations"
  },
  "/accounts/{accountId}/transactions/{transactionId}": {
    "$ref": "./schemas_with_one_of/transaction_paths.json#/transaction"
  },
  "/transactions": {
    "$ref": "./schemas_with_one_of/transaction_paths.json#/list_transactions"
  },
  "/disputes": {
    "$ref": "./schemas_with_one_of/dispute_paths.json#/list_disputes"
  },
  "/disputes/{disputeId}": {
    "$ref": "./schemas_with_one_of/dispute_paths.json#/get_dispute"
  },
  "/repayments": {
    "$ref": "./schemas_with_one_of/repayment_paths.json#/repayments"
  },
  "/repayments/{repaymentId}": {
    "$ref": "./schemas_with_one_of/repayment_paths.json#/repayment"
  },
  "/check-payments": {
    "$ref": "./schemas_with_one_of/check_payment_paths.json#/check_payments"
  },
  "/check-payments/{checkPaymentId}": {
    "$ref": "./schemas_with_one_of/check_payment_paths.json#/check_payment"
  },
  "/check-payments/{checkPaymentId}/approve": {
    "$ref": "./schemas_with_one_of/check_payment_paths.json#/check_payment_approve"
  },
  "/stop-payments": {
    "$ref": "./schemas_with_one_of/stop_payment_paths.json#/stop_payments"
  },
  "/stop-payments/{stop_payment_id}": {
    "$ref": "./schemas_with_one_of/stop_payment_paths.json#/stop_payment"
  },
  "/stop-payments/disable": {
    "$ref": "./schemas_with_one_of/stop_payment_paths.json#/disable_stop_payment"
  }
}

replace_paths_with_one_of("openapi.json", new_paths)

# Generate the Ruby SDK using openapi-generator-cli
command = "openapi-generator-cli generate -g ruby -i openapi.json -o unit/e2e_tests/ruby"
stdout, stderr, status = Open3.capture3(command)

if status.success?
  puts "SDK generated successfully."
else
  puts "Error generating SDK: #{stderr}"
end
