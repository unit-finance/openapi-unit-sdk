# frozen_string_literal: true

require 'spec_helper'

RSpec.describe 'Repayment' do
  before do
    configure_tests
  end

  describe 'test an instance of Repayment' do
    let(:api_instance) { OpenapiClient::CreateARepaymentApi.new(OpenapiClient::ApiClient.new(configuration)) }

    it 'should create an instance of book Repayment' do
      attributes = OpenapiClient::CreateBookRepaymentAttributes.new(description: "test", amount: 1, transaction_summary_override: "override", tags: { purpose: "test" }, idempotency_key: "3a1a33be-4e12-4603-9ed0-820922389fb8")
      relationships = OpenapiClient::CreateBookRepaymentRelationships.new(account: { "data": { "type": 'depositAccount', "id": '27573' } },
                                                                          credit_account: { "data": { "type": 'creditAccount', "id": '956423' } },
                                                                          counterparty_account: { "data": { "type": 'account', "id": '36981' } })
      request = { data: OpenapiClient::CreateBookRepayment.new(type: 'bookRepayment', attributes: attributes, relationships: relationships).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('bookRepayment')
    end

    it 'should create an instance of ach Repayment' do
      attributes = OpenapiClient::CreateAchRepaymentAttributes.new(description: "test", amount: 1, addenda: "test", tags: { purpose: "test" },
                                                                   same_day: false, idempotency_key: "test")
      relationships = OpenapiClient::CreateAchRepaymentRelationships.new(account: { "data": { "type": 'depositAccount', "id": '27573' } },
                                                                         credit_account: { "data": { "type": 'creditAccount', "id": '956423' } },
                                                                         counterparty: { "data": { "type": 'counterparty', "id": '396872' } })
      request = { data: OpenapiClient::CreateAchRepayment.new(type: 'achRepayment', attributes: attributes, relationships: relationships).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('achRepayment')
    end
  end
end